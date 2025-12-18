import React from 'react';
import { Shield, Lock, Eye, UserCheck, FileText, Database } from 'lucide-react';

/**
 * Componente de informações sobre privacidade e segurança LGPD
 */
export default function PrivacyInfo() {
  return (
    <div className="max-w-4xl mx-auto p-6 space-y-6">
      <div className="bg-gradient-to-r from-blue-600 to-blue-700 text-white p-8 rounded-lg shadow-lg">
        <div className="flex items-center space-x-4 mb-4">
          <Shield className="w-12 h-12" />
          <div>
            <h1 className="text-3xl font-bold">Segurança Total</h1>
            <p className="text-blue-100">Conformidade com LGPD e criptografia de ponta a ponta</p>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-white p-6 rounded-lg shadow-md border border-gray-200">
          <div className="flex items-center space-x-3 mb-4">
            <Lock className="w-8 h-8 text-blue-600" />
            <h2 className="text-xl font-bold text-gray-800">Criptografia</h2>
          </div>
          <ul className="space-y-2 text-sm text-gray-600">
            <li className="flex items-start">
              <span className="text-blue-600 mr-2">•</span>
              <span>AES-256-GCM para dados em repouso</span>
            </li>
            <li className="flex items-start">
              <span className="text-blue-600 mr-2">•</span>
              <span>TLS 1.3 para dados em trânsito</span>
            </li>
            <li className="flex items-start">
              <span className="text-blue-600 mr-2">•</span>
              <span>BCrypt para senhas</span>
            </li>
            <li className="flex items-start">
              <span className="text-blue-600 mr-2">•</span>
              <span>JWT com rotação automática</span>
            </li>
          </ul>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-md border border-gray-200">
          <div className="flex items-center space-x-3 mb-4">
            <Eye className="w-8 h-8 text-green-600" />
            <h2 className="text-xl font-bold text-gray-800">Auditoria</h2>
          </div>
          <ul className="space-y-2 text-sm text-gray-600">
            <li className="flex items-start">
              <span className="text-green-600 mr-2">•</span>
              <span>Logs de todos os acessos</span>
            </li>
            <li className="flex items-start">
              <span className="text-green-600 mr-2">•</span>
              <span>Rastreamento de modificações</span>
            </li>
            <li className="flex items-start">
              <span className="text-green-600 mr-2">•</span>
              <span>IP e timestamp registrados</span>
            </li>
            <li className="flex items-start">
              <span className="text-green-600 mr-2">•</span>
              <span>Relatórios de conformidade</span>
            </li>
          </ul>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-md border border-gray-200">
          <div className="flex items-center space-x-3 mb-4">
            <UserCheck className="w-8 h-8 text-purple-600" />
            <h2 className="text-xl font-bold text-gray-800">Seus Direitos</h2>
          </div>
          <ul className="space-y-2 text-sm text-gray-600">
            <li className="flex items-start">
              <span className="text-purple-600 mr-2">•</span>
              <span>Acesso aos seus dados</span>
            </li>
            <li className="flex items-start">
              <span className="text-purple-600 mr-2">•</span>
              <span>Correção de informações</span>
            </li>
            <li className="flex items-start">
              <span className="text-purple-600 mr-2">•</span>
              <span>Portabilidade de dados</span>
            </li>
            <li className="flex items-start">
              <span className="text-purple-600 mr-2">•</span>
              <span>Revogação de consentimento</span>
            </li>
          </ul>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-md border border-gray-200">
          <div className="flex items-center space-x-3 mb-4">
            <FileText className="w-8 h-8 text-orange-600" />
            <h2 className="text-xl font-bold text-gray-800">Conformidade</h2>
          </div>
          <ul className="space-y-2 text-sm text-gray-600">
            <li className="flex items-start">
              <span className="text-orange-600 mr-2">•</span>
              <span>LGPD (Lei 13.709/2018)</span>
            </li>
            <li className="flex items-start">
              <span className="text-orange-600 mr-2">•</span>
              <span>CFM (Resolução 1.821/2007)</span>
            </li>
            <li className="flex items-start">
              <span className="text-orange-600 mr-2">•</span>
              <span>Encarregado de dados (DPO)</span>
            </li>
            <li className="flex items-start">
              <span className="text-orange-600 mr-2">•</span>
              <span>Políticas documentadas</span>
            </li>
          </ul>
        </div>
      </div>

      <div className="bg-white p-6 rounded-lg shadow-md border border-gray-200">
        <div className="flex items-center space-x-3 mb-4">
          <Database className="w-8 h-8 text-indigo-600" />
          <h2 className="text-xl font-bold text-gray-800">Tratamento de Dados</h2>
        </div>
        <div className="space-y-3 text-sm text-gray-600">
          <p>
            <strong>Dados coletados:</strong> Nome, CPF, email, telefone, endereço, data de nascimento, 
            informações médicas e histórico de consultas.
          </p>
          <p>
            <strong>Finalidade:</strong> Prestação de serviços médicos, agendamento de consultas, 
            comunicação com o paciente e acionamento de pagamentos via parceiro LunaPay (fora do totem).
          </p>
          <p>
            <strong>Compartilhamento:</strong> Dados não são compartilhados com terceiros, exceto 
            o processador de pagamento LunaPay (com seu consentimento; transações externas) e autoridades legais quando exigido.
          </p>
          <p>
            <strong>Retenção:</strong> Dados mantidos pelo período necessário para prestação dos serviços 
            e cumprimento de obrigações legais (mínimo 20 anos para prontuários - CFM).
          </p>
        </div>
      </div>

      <div className="bg-blue-50 border border-blue-200 p-6 rounded-lg">
        <h3 className="font-bold text-blue-800 mb-2">Contato do Encarregado (DPO)</h3>
        <p className="text-sm text-blue-700">
          Para exercer seus direitos ou esclarecer dúvidas sobre proteção de dados:
        </p>
        <div className="mt-3 text-sm text-blue-800">
          <p><strong>Email:</strong> dpo@lunavita.com</p>
          <p><strong>Telefone:</strong> (85) 4002-8922</p>
        </div>
      </div>
    </div>
  );
}
